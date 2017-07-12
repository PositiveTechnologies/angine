interface Entity {
  abstract id: str;
}

interface SQLEntity <: Entity {
  database: str;
  table: str;
  column: str;
  level: int;
  tags: [str];
}

interface Subject <: Entity {
  name: str;
  level: int;
  tags: [str];
}
