class Policy:
    """
    Lua policy storage.
    """
    def __init__(self, text):
        self.text = text

    @classmethod
    def from_file(cls, file):
        with open(file) as f:
            text = f.read()
        return cls(text)
