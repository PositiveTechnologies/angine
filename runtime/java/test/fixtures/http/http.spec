interface Entity {
  abstract id: str;
}

interface UrlEntity <: Entity {
  path: str;
  level: int;
  tags: [str];
}

interface Subject <: Entity {
  name: str;
  roles: [str];
  level: int;
  tags: [str];
  abstract ip: str;
}
