import subprocess

from .param import ROOT_DIR, INTEGRATION_DIR


# static
def __get_args_string(input, output):
    return f"--input-dir={input}" + \
           (f" --output-dir={output}", "")[output is None]


# static
def _print_info(command, out, err, code):
    print("$", command)
    print(f"{' stdout ':-^25}", out, sep='\n')
    print(f"{' stderr ':-^25}", err, sep='\n')
    print(f"{' code ':-^25}", code, sep='\n')


# static
def _exec_command(command, cwd, show_info=True):
    process = subprocess.run(
        command,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        cwd=cwd,
        shell=True)

    out = process.stdout.decode('utf-8')
    err = process.stderr.decode('utf-8')
    code = process.returncode

    if show_info:
        _print_info(command, out, err, code)

    return out, err, code


# static
def exec_project(args_string, show_info=True):
    return _exec_command(f"./gradlew run --args=\"{args_string}\"",
                         ROOT_DIR, show_info)


def exec_build(input, output=None, show_info=True):
    return exec_project("build " + __get_args_string(input, output),
                         show_info)


def exec_vnu(input, show_info=True):
    return _exec_command(f"pipenv run html5validator --root {input}",
                         INTEGRATION_DIR,
                         show_info)
