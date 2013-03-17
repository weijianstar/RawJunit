
class TextTestRunner:
    def __init__(self, verbosity=1):
        self.verbosity = verbosity


class TestLoader:

    def loadTestsFromModule(self, module):
        
        tests = []
        for name in dir(module):
            print name

defalutTestLoader = TestLoader()

class TestProgram:
    
    def __init__(self, module='__main__', testLoader = defalutTestLoader):
        self.module = __import__(module)
        self.testLoader = testLoader
        self.test = self.testLoader.loadTestsFromModule(self.module)

    def runTest(self):
        if self.testRunner is None:
            self.testRunner = TextTestRunner(verbosity=self.verbosity)

main = TestProgram

if __name__ == '__main__':
    main(module=None)
