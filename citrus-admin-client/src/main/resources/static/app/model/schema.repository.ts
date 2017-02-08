export class SchemaRepository {

    constructor() {
        this.schemas = new Schemas();
    }

    public id: string;
    public schemaMappingStrategy: string;
    public schemas: Schemas;
}

export class Schemas {

    constructor() {
        this.schemas = [];
        this.references = [];
    }

    public schemas: Schema[];
    public references: SchemaReference[];
}

export class Schema {

    constructor() { }

    public id: string;
    public location: string;
}

export class SchemaReference {

    constructor() { }

    public schema: string;
}