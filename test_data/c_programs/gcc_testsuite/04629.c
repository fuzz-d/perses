



struct S { int a; char b; long long c; short d[10]; };
struct T { char a; long long b; };
struct U { char a; int b; int c; long long d; struct S e; struct T f; } __attribute__((packed));
struct V { long long a; struct S b; struct T c; struct U u; } v;

__attribute__((noinline, noclone)) int
foo (struct S *p)
{
  volatile int i;
  i = p->a;
  i = p->a;
  i = p->a;
  i = p->a;
  return p->a;
}

int
main ()
{
  if (foo (&v.u.e))
    __builtin_abort ();
  return 0;
}