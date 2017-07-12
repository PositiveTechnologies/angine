interface Entity {
  abstract id: str;
}

interface UrlEntity <: Entity {
  path: str;
}

interface Subject <: Entity {
  name: str;
  roles: [str];
  abstract ip: str;
}
