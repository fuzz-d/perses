


int
fn1 (int a, int b)
{
  int tem1 = a + b;
  int tem2 = a & b;
  return tem1 - tem2;
}

int
fn2 (int a, int b)
{
  int tem1 = b + a;
  int tem2 = a & b;
  return tem1 - tem2;
}

int
fn3 (int a, int b)
{
  int tem1 = a + b;
  int tem2 = b & a;
  return tem1 - tem2;
}

int
fn4 (int a, int b)
{
  int tem1 = b + a;
  int tem2 = b & a;
  return tem1 - tem2;
}
