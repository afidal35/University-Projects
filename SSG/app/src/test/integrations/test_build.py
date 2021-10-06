import pytest

# noinspection PyUnresolvedReferences
from src import EXAMPLE_DIR, exec_build, exec_vnu, output


@pytest.mark.parametrize(
    "input",
    [
        pytest.param(EXAMPLE_DIR / "minimal-template",
                     id="build-minimal"),
    ]
)
def test_build_good(input, output):
    out, err, code = exec_build(input, output)
    assert code == 0

@pytest.mark.parametrize(
    "input",
    [
        pytest.param(EXAMPLE_DIR / "minimal-template",
                     id="build-minimal"),
    ]
)
def test_html5_validation(input, output):
    out, err, code = exec_build(input, output)
    assert code == 0

    out, err, code = exec_vnu(output)
    assert err == ""
    assert code == 0