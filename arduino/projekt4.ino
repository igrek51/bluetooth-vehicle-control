int enabledPins = 12; // liczba dostępnych pinów do zmiany stanu

char delimiter = '\n'; // separator poleceń z BT
String rxBuffer; // bufor danych otrzymanych z BT

unsigned long lastHeartBeat = 0; // czas od ostatniego komunikatu HeartBeat utrzymującego połączenie
unsigned long HEARTBEAT_TIMEOUT = 2000; // maksymalny czas oczekiwania na kolejny HeartBeat [ms]

void setup() {
  // inicjalizacja pinów
  for(int i=1; i<=enabledPins; i++){
    int pin = apiNumber2Pin(i);
    pinMode(pin, OUTPUT);
  }
  resetAllPins();
  // komunikacja BT
  Serial.begin(9600); // baud rate
  rxBuffer = "";
  send("Hello");

  updateHeartBeat();
}

void loop() {
  while (Serial.available())  { //if there is data being recieved
    rxBuffer += char(Serial.read()); //read it
  }
  if (rxBuffer.length() > 0){
    String cmd = popCommand();
    if (cmd.length() > 0) { // jeśli przyszła pełna komenda
      parseCommand(cmd);
    }
  }
  // sprawdzenie, czy nie minął za długi czas od ostatniego heartBeat
  if (millis() > lastHeartBeat + HEARTBEAT_TIMEOUT){
    // automatyczne wyłączenie zasilania silników w przypadku utraty łączności
    resetAllPins();
    updateHeartBeat();
    send("HeartBeat timeout");
  }

  delay(1);
}

/** konwertuje numer pinu z API na numer pinu na płytce */
int apiNumber2Pin(int apiNumber){
  if(apiNumber == 1) return 2;
  if(apiNumber == 2) return 3;
  if(apiNumber == 3) return 4;
  if(apiNumber == 4) return 5;
  if(apiNumber == 5) return 6;
  if(apiNumber == 6) return 7;
  if(apiNumber == 7) return 8;
  if(apiNumber == 8) return 9;
  if(apiNumber == 9) return 10;
  if(apiNumber == 10) return 11;
  if(apiNumber == 11) return 12;
  if(apiNumber == 12) return 13;
  return -1;
}

void resetAllPins(){
  for(int i=1; i<=enabledPins; i++){
    int pin = apiNumber2Pin(i);
    bool defaultState = LOW;
//    if (pin == 7 || pin == 8){
//      // odwrotna logika
//      defaultState = HIGH;
//    }
    digitalWrite(pin, defaultState);
  }
}

int apiNumber2Pin(String apiNumber){
  return apiNumber2Pin(apiNumber.toInt());
}

void send(String txt) {
  Serial.println(txt);
}

void error(String message) {
  send("ERROR: " + message);
}

String popCommand() {
  // wczytanie pierwszej pełnej komendy zakończonej znakiem \n
  int firstIndex = rxBuffer.indexOf(delimiter);
  if (firstIndex == -1) { // brak pełnej komendy - czekamy dalej
    return "";
  }
  String cmd = rxBuffer.substring(0, firstIndex); // wycięcie bez delimitera
  cmd.replace("\r", ""); // usunięcie śmieci
  // reszta bez wyciętej komendy
  rxBuffer = rxBuffer.substring(firstIndex + 1);
  return cmd;
}

void parseCommand(String cmd) {
  //send("received: " + cmd);
  // parsowanie po 3 znakowym kodzie
  if (cmd.length() >= 3) {
    String code = cmd.substring(0, 3);
    code.toUpperCase();
    String rest = cmd.substring(3);
    if (code == "TST") { // test connection
      commandTest();
    } else if (code == "SET") { // set HIGH pin state (PWM 100%)
      commandSet(rest, true);
    } else if (code == "RST") { // set LOW pin state
      commandSet(rest, false);
    } else if (code == "PWM") { // set PWM factor
      commandPWM(rest);
    } else if (code == "STA") { // resend all pin states
      commandStatus();
    } else if (code == "RTA") { // reset all pin states
      resetAllPins();
    } else if (code == "HBT") { // heartbeat - keep alive
      updateHeartBeat();
    } else {
      error("Unknown command: " + cmd);
    }
    // any command updates keep alive timeout
    updateHeartBeat();
  } else {
    error("Command is too short");
  }
}

void commandTest() {
  send("OK");
}

void commandStatus() {
  // print pins states
  String out = "PINS:";
  for(int i=1; i<=enabledPins; i++){
    // TODO odczytanie pinu powoduje reset PWMa na wyłączenie
    out += digitalRead(apiNumber2Pin(i)) == HIGH ? "1" : "0";
  }
  send(out);
}

void commandSet(String pin, boolean enable) {
  pin.replace(" ", ""); //poprawny format SET 1 lub SET1
  int pinNumber = apiNumber2Pin(pin);
  if (pinNumber < 0){
    error("invalid pin number");
    return;
  }
  digitalWrite(pinNumber, enable);
}

void commandPWM(String rest){
  // format: numer pinu (1 znak) + " " + procent współczynnika wypełnienia (max 3 znaki)
  // obcięcie pierwszej opcjonalnej spacji
  if (rest.startsWith(" ")){
    rest = rest.substring(1);
  }
  if(rest.length() < 3){
    error("too short PWM command");
    return;
  }
  // wyciąganie parametrów
  String pinStr = rest.substring(0, 1);
  String pulseWidthStr = rest.substring(2);
  int pinApi = pinStr.toInt();
  int pulseWidth= pulseWidthStr.toInt();
  // walidacja numeru pinu
  int pinNumber = apiNumber2Pin(pinApi);
  if (pinNumber < 0){
    error("invalid pin number");
    return;
  }
  // ustawienie wyjścia jako PWM
  analogWrite(pinNumber, 255 * pulseWidth / 100); // maksymalna wartość wypełnienia to 255
}

void updateHeartBeat(){
  lastHeartBeat = millis();
}

