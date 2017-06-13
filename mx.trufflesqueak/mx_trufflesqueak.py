import os
import mx


_suite = mx.suite('trufflesqueak')
_mx_graal = mx.suite("compiler", fatalIfMissing=False)


def _graal_heuristics_options(_mx_graal):
    if _mx_graal:
        return  [
            '-XX:+UseJVMCICompiler',
            '-Djvmci.Compiler=graal',
            '-Dgraal.TraceTruffleCompilation=true',
            # '-Dgraal.TruffleCompileImmediately=true',
            '-Dgraal.TruffleCompilationThreshold=10',
            '-Dgraal.TraceTrufflePerformanceWarnings=true',
            '-Dgraal.TruffleCompilationExceptionsArePrinted=true'
        ]
    else:
        return []


def _extract_squeak_args(args):
    squeakArgs = []
    other = []
    i = 0
    while i < len(args):
        arg = args[i]
        i += 1
        if arg == "-debug":
            other += ["-Xdebug",
                      "-Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y"]
        elif arg == "-dump":
            other += ["-Dgraal.Dump=",
                      "-Dgraal.MethodFilter=Truffle.*",
                      "-Dgraal.TruffleBackgroundCompilation=false",
                      "-Dgraal.TraceTruffleCompilation=true",
                      "-Dgraal.TraceTruffleCompilationDetails=true"]
        elif arg == "-disassemble":
            other += ["-XX:CompileCommand=print,*OptimizedCallTarget.callRoot",
                      "-XX:CompileCommand=exclude,*OptimizedCallTarget.callRoot",
                      "-Dgraal.TruffleBackgroundCompilation=false",
                      "-Dgraal.TraceTruffleCompilation=true",
                      "-Dgraal.TraceTruffleCompilationDetails=true"]
        elif arg in ["-r", "-m"]:
            squeakArgs.append(arg)
            squeakArgs.append(args[i])
            i += 1
        elif arg in ["--help"]:
            squeakArgs.append(arg)
        elif arg.endswith(".image"):
            squeakArgs.append(arg)
        else:
            other.append(arg)
    return other, squeakArgs


def check_vm(vm_warning=True, must_be_jvmci=False):
    if not _mx_graal:
        if must_be_jvmci:
            print '** Error ** : graal compiler was not found!'
            sys.exit(1)

        if vm_warning:
            print '** warning ** : graal compiler was not found!! Executing using standard VM..'


def get_jdk():
    if _mx_graal:
        tag = 'jvmci'
    else:
        tag = None
    return mx.get_jdk(tag=tag)


def squeak(args, extraVmArgs=None, env=None, jdk=None, **kwargs):
    """run a Python program or shell"""
    if not env:
        env = os.environ

    if not 'TRUFFLESQUEAK_HOME' in env:
        env['TRUFFLESQUEAK_HOME'] = _suite.dir

    check_vm_env = env.get('TRUFFLESQUEAK_MUST_USE_GRAAL', False)
    if check_vm_env:
        if check_vm_env == '1':
            check_vm(must_be_jvmci=True)
        elif check_vm_env == '0':
            check_vm()

    vmArgs, squeakArgs = _extract_squeak_args(args)
    vmArgs, nonVMargs = mx.extract_VM_args(vmArgs)
    squeakArgs += nonVMargs
    vmArgs += ['-cp', mx.classpath(["de.hpi.swa.trufflesqueak"])]

    if not jdk:
        jdk = get_jdk()

    vmArgs += _graal_heuristics_options(_mx_graal)

    # default: assertion checking is enabled
    if extraVmArgs is None or not '-da' in extraVmArgs:
        vmArgs += ['-ea', '-esa']

    if extraVmArgs:
        vmArgs += extraVmArgs

    vmArgs.append("de.hpi.swa.trufflesqueak.TruffleSqueakMain")
    return mx.run_java(vmArgs + squeakArgs, jdk=jdk, **kwargs)


mx.update_commands(_suite, {
    # new commands
    'squeak' : [squeak, '[image options|@VM options]'],
})
