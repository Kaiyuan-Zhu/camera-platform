

#include <Wire.h>
#include "Adafruit_TMP007.h"
#include <Timers.h>

Adafruit_TMP007 tmp007;
const unsigned char TEMP = 1;
const unsigned int period = 1000;


unsigned char TestForKey(void);
void RespToKey(void);
unsigned char TestTimerExpired(unsigned char);
void RespToTimerExpired(unsigned char);
void StartRead(void);
void StopRead(void);

void setup() { 
  Serial.begin(9600);


  if (! tmp007.begin()) {
    Serial.println("No sensor found");
    while (1);
  }
  TMRArd_SetTimer(TEMP,period);
  
}

void loop() {
  if (TestForKey()) RespToKey();
  if (TMRArd_IsTimerActive(TEMP) && TestTimerExpired(TEMP)) RespToTimerExpired(TEMP);
}

void readTemp(void) {
   float objt = tmp007.readObjTempC();
   Serial.print(objt); 
   Serial.print(',');
   float diet = tmp007.readDieTempC();
   Serial.println(diet); 
}

unsigned char TestForKey(void) {
  unsigned char KeyEventOccurred;
  
  KeyEventOccurred = Serial.available();
  return KeyEventOccurred;
}

void RespToKey(void) {
  unsigned char theKey;

  
  theKey = Serial.read();

  //Serial.println(theKey);

  
switch(theKey){
  case 97: {StartRead(); break;}
  case 98: {StopRead(); break;}


  default: { Serial.println("press keys from a b"); }
}
}

unsigned char TestTimerExpired(unsigned char timer) {
  return (unsigned char)TMRArd_IsTimerExpired(timer);
}

void RespToTimerExpired(unsigned char timer)  {
  switch(timer){
    case TEMP: {
      TMRArd_ClearTimerExpired(TEMP);
      TMRArd_InitTimer(TEMP,period);
      readTemp();
      break;
    }
  }                
}

void StartRead(void){
  TMRArd_StartTimer(TEMP);
}
void StopRead(void){
  
  TMRArd_StopTimer(TEMP);
  
}
