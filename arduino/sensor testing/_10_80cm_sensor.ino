#include <DistanceGP2Y0A21YK.h>

DistanceGP2Y0A21YK Dist;
int distance;

void setup()
{
  Serial.begin(9600);
  Dist.begin(A3);
}

void loop()
{
  int R;
  R = 6787 / (analogRead(A3) - 3) - 4;
  //distance = Dist.getDistanceCentimeter()/0.39370;
  Serial.print("\nDistance in centimeters: ");
  Serial.print(R);  
  delay(500); //make it readable
}
