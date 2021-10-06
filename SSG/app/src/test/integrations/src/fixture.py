from tempfile import TemporaryDirectory

import pytest


@pytest.fixture
def output():
    with TemporaryDirectory(prefix="test_build") as output:
        yield output
