import starunit


class TestSequenceFunctions(starunit.TestCase):
    def setUp(self):
        print "setUp"

    def test_shuffle(self):
        print "test_shuffle"

    def test_choice(self):
        print "test_choice"

    def test_sample(self):
        print "test_sample"

if __name__ == '__main__':
    starunit.main()
