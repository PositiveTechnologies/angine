class Handlers(object):
    def __init__(self):
        pass

    def check_roles(self, subject_id, entity_id):
        pass

    def check_groups(self, subject_id, entity_id):
        pass


def getter(obj, attr_name):
    if hasattr(obj, attr_name):
        return getattr(obj, attr_name)
    else:
        return None


def setter(obj, attr_name, value):
    setattr(obj, attr_name, value)

