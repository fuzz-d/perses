pragma experimental SMTChecker;

contract C
{
	function f(uint x) public pure {
		while (x == 0) {
			++x;
			break;
			++x;
		}
		assert(x == 2);
	}
}
// ====
// SMTSolvers: z3
// SMTIgnoreCex: yes
// ----
// Warning 5740: (120-123): Unreachable code.
// Warning 6328: (131-145): CHC: Assertion violation happens here.
