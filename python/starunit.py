
import types
import sys
import time

class TestResult:
    def __init__(self):
        self.failures = []
        self.errors = []
        self.testRun = 0
        self.should = 0
        self.shouldStop = 0
    
    def startTest(self, test):
        self.testRun = self.testRun + 1

    def stopTest(self, test):
        pass

    def addError(self, test, err):
        self.errors.append((test, err))

    def addFailure(self, test, err):
        self.failures.append((test,err))

    def addSuccess(self, test):
        pass

    def wasSuccessful(self):
        return len(self.failures) == len(self.errors) == 0
    
    def stop(self):
        self.shouldStop = 1

    def _repr(self):
        return "<%s run=%i errors=%i failures=%i" % \
                (self.__class__, self.testRun, len(self.errors),
                        len(self.failures))

class TestCase:
    def __init__(self, methodName='runTest'):
        try:
            self.__testMethodName = methodName
            testMethod = getattr(self, methodName)
            self.__testMethodDoc = testMethod.__doc__
        except AttributeError:
            raise ValueError, "no such test method in %s: %s" % \
                    (self.__class__, methodName)
    
    def __str__(self):
        return "%s (%s)" % (self.__testMethodName, self.__class__)

    def __repr__(self):
        return "<%s testMethod=%s>" % \
                (self.__class__, self.__testMethodName)
    
    def shortDescription(self):
        doc = self.__testMethodDoc
        return doc and string.strip(string.split(doc,"\n")[0]) or None

    def setUp(self):
        pass
    
    def defaultTestResult(self):
        return TestResult()

    def __call__(self, result=None):
        if result is None : result = self.defaultTestResult()
        result.startTest(self)
        testMethod = getattr(self, self.__testMethodName)
        try:
            try:
                self.setUp()
            except:
                result.addError(self, self,__exc_info())
                return

            ok = 0
            try:
                testMethod()
                ok = 1
            except self.failureException, e:
                result.addFailure(self, self.__exc_info())
            except:
                result.addError(self, self.__exc_info())

            try:
                self.tearDown()
            except:
                result.addError(self, self._exc_info())
                ok = 0
            if ok: result.addSuccess(self)
        finally:
            result.stopTest(self)

    def tearDown(self):
        pass
    
    def countTestCases(self):
        return 1

    def _exc_info(self):
        exctype, excvalue, tb = sys.exc_info()
        newtb = tb.tb_next
        if newtb is None:
            return (exctype, excvalue, tb)
        return (exctype, excvalue, newtb)

class TestSuite:

    def __init__(self, tests=()):
        self._tests = []
        self.addTests(tests)

    def __repr__(self):
        return "<%s tests=%s>" % (self.__class__, self._tests)
    
    __str__ = __repr__
    
    def addTest(self, test):
        self._tests.append(test)

    def addTests(self, tests):
        for test in tests:
            self.addTest(test)

    def __call__(self, result):
        for test in self._tests:
            if result.shouldStop:
                break
            test(result)
        return result

class _TextTestResult(TestResult):
    separator1 = '=' * 70
    separator2 = '-' * 70

    def __init__(self, stream, descriptions, verbosity):
        TestResult.__init__(self)
        self.stream = stream
        self.dots = verbosity == 1
        self.descriptions = descriptions

    def getDescription(self, test):
        if self.descriptions:
            return test.shortDescription() or str(test)
        else:
            return str(test)

    def startTest(self, test):
        TestResult.startTest(self, test)
    
    def addSuccess(self, test):
        TestResult.addSuccess(self, test)
        if self.dots:
            self.stream.write('.')

    def addError(self, test, err):
        TestResult.addError(self, test, err)
        if self.dots:
            self.stream.write('E')
        if err[0] is KeyboardInterrupt:
            self.shouldStop = 1

    def addFailure(self, test, err):
        TestResult.addFailure(self, test, err)
        if self.dots:
            self.stream.write('F')

    def printErrors(self):
        if self.dots:
            self.stream.writeln()
        self.printErrorList('ERROR', self.errors)
        self.printErrorList('FAIL', self.failures)

    def printErrorList(self, flavour, errors):
        for test, err in errors:
            self.stream.writeln(self.separator1)
            self.stream.writeln("%s: %s" % (flavor, self.getDescription(test)))
            self.stream.writeln(self.separator2)


class TextTestRunner:
    def __init__(self,stream=sys.stderr,descriptions=1,verbosity=1):
        self.verbosity = verbosity
        self.stream = _WritelnDecorator(stream)
        self.descriptions = descriptions
    
    def _makeResult(self):
        return _TextTestResult(self.stream, self.descriptions, self.verbosity)

    def run(self, test):
        result = self._makeResult()
        startTime = time.time()
        test(result)
        stopTime = time.time()
        timeTaken = float(stopTime - startTime)
        result.printErrors()
        
class _WritelnDecorator:
    def __init__(self, stream):
        self.stream = stream

    def __getattr__(self, attr):
        return getattr(self.stream, attr)

    def writeln(self, *args):
        if args: apply(self.write, args)
        self.write('\n')

class TestLoader:

    testMethodPrefix = 'test'
    suiteClass = TestSuite
    sortTestMethodsUsing = cmp

    def loadTestsFromTestCase(self, testCaseClass):
        fnNames = self.getTestCaseNames(testCaseClass)
        return self.suiteClass(map(testCaseClass, fnNames))

    def loadTestsFromModule(self, module):
        tests = []
        for name in dir(module):
            obj = getattr(module, name)
            if type(obj) == types.ClassType and issubclass(obj, TestCase):
                tests.append(self.loadTestsFromTestCase(obj))
        return self.suiteClass(tests)


    def getTestCaseNames(self, testCaseClass):
        """Return a sorted sequence of menthod names found within testCaseClass"""
        testFnNames = filter(lambda n,p=self.testMethodPrefix: n[:len(p)] == p,dir(testCaseClass))

        if self.sortTestMethodsUsing:
            testFnNames.sort(self.sortTestMethodsUsing)
        return testFnNames

defalutTestLoader = TestLoader()

class TestProgram:
    
    def __init__(self, module='__main__', testRunner=None, testLoader = defalutTestLoader):
        self.module = __import__(module)
        self.verbosity = 1
        self.testLoader = testLoader
        self.test = self.testLoader.loadTestsFromModule(self.module)
        self.testRunner = testRunner
        self.runTests()

    def runTests(self):
        if self.testRunner is None:
            self.testRunner = TextTestRunner(verbosity=self.verbosity)
        result = self.testRunner.run(self.test)

main = TestProgram

if __name__ == '__main__':
    main(module=None)
