#include <Servo.h> 
#include <Timers.h>

unsigned char TestForKey(void);
void RespToKey(void);
void Clockwise(void);
void Stop(void);
void CounterClockwise(void);
unsigned char TestTimerExpired(unsigned char);
void RespToTimerExpired(unsigned char);


Servo servo;
const int middle = 95;
const unsigned char ROTATE = 1;
const unsigned char REVERSE = 2;
const int period = 150;  //to change angle of rotation, simply change the period
const int speedArrayClockwise[] = {95, 96, 96, 96, 96, 96, 96, 96, 96, 96, 
96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 96, 95};
const int speedArrayCounterClockwise[] = {95, 94, 94, 94, 94, 94, 94, 
94, 94, 94, 94, 94, 94, 94, 94, 94, 94, 94, 94, 94, 94, 94, 94, 94, 94, 95};
const int clockwiseLength = sizeof(speedArrayClockwise)/2;
const int counterclockwiseLength = sizeof(speedArrayCounterClockwise)/2;
const int servopin = 9;

int index = 0;
void setup() 
{ 
  servo.attach(servopin); 
  Stop();
    while (!Serial);  
  Serial.begin(9600);
  
  TMRArd_SetTimer(ROTATE,period);
  TMRArd_SetTimer(REVERSE,period);
  
} 

void loop() {
  if (TestForKey()) RespToKey();
  if (TMRArd_IsTimerActive(ROTATE) && TestTimerExpired(ROTATE)) RespToTimerExpired(ROTATE);
  if (TMRArd_IsTimerActive(REVERSE) && TestTimerExpired(REVERSE)) RespToTimerExpired(REVERSE);

} 

unsigned char TestForKey(void) {
  unsigned char KeyEventOccurred;
  
  KeyEventOccurred = Serial.available();
  return KeyEventOccurred;
}

void RespToKey(void) {
  unsigned char theKey;

  
  theKey = Serial.read();

  Serial.println(theKey);

  
switch(theKey){
  case 97: {servo.write(96); break;}  //press "a" to start rotate clockwise
  case 98: {servo.write(94); break;}  //press "b" to start rotate counterclockwise
  case 99: {Stop(); break; }  //press "c" to stoop rotating
  case 100: {Stop(); TMRArd_StartTimer(ROTATE); break; }  //press "d" to start a full rotate cycle clockwise
  case 101: {Stop(); TMRArd_StartTimer(REVERSE);break; }  //press "e" to start a full rotate cycle counterclockwise

  default: { Serial.println("press keys from a b c"); }
}
}

void Clockwise(void){
    index++;
    servo.write(speedArrayClockwise[index]);

}

void CounterClockwise(void){
    index++;
    servo.write(speedArrayCounterClockwise[index]);
}

unsigned char TestTimerExpired(unsigned char timer) {
  return (unsigned char)TMRArd_IsTimerExpired(timer);
}

void Stop(void) {
      servo.write(middle);
      TMRArd_StopTimer(ROTATE);
      TMRArd_StopTimer(REVERSE);
      index = 0;
}

void RespToTimerExpired(unsigned char timer)  {
  switch(timer){
    case ROTATE: {
      TMRArd_ClearTimerExpired(ROTATE);
      TMRArd_InitTimer(ROTATE,period);
      Clockwise();
      if(index>=clockwiseLength-1) {
            TMRArd_StopTimer(ROTATE);
            index = 0;
        }
      break;
    }
    case REVERSE: {
      TMRArd_ClearTimerExpired(REVERSE);
      TMRArd_InitTimer(REVERSE,period);
      CounterClockwise();
      if(index>=counterclockwiseLength-1) {
            TMRArd_StopTimer(REVERSE);
            index = 0;
      }
      break;
    }
  }                
}
