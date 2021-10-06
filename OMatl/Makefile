EXE=src/omatl.exe
TESTS=tests/

test: 
	dune runtest ${TESTS}

binary:
	dune build ${EXE}

byte:
	dune build ${EXE:.exe=.bc}

clean:
	dune clean
