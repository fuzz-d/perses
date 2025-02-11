pragma experimental SMTChecker;

contract LoopFor2 {
	uint[] a;
	function p() public {
		a.push();
	}
	function testUnboundedForLoop(uint n, uint[] memory b, uint[] memory c) public {
		require(n < a.length);
		require(n < b.length);
		require(n < c.length);
		b[0] = 900;
		a = b;
		require(n > 0 && n < 100);
		uint i;
		// Disabled because of Spacer nondeterminism.
		/*
		while (i < n) {
			// Accesses are safe but oob is reported due to potential aliasing after c's assignment.
			b[i] = i + 1;
			c[i] = b[i];
			++i;
		}
		*/
		// Fails due to aliasing, since both b and c are
		// memory references of same type.
		// Removed because current Spacer seg faults in cex generation.
		//assert(b[0] == c[0]);
		assert(a[0] == 900);
		// Removed because current Spacer seg faults in cex generation.
		//assert(b[0] == 900);
	}
}
// ====
// SMTSolvers: z3
// ----
// Warning 2072: (313-319): Unused local variable.
