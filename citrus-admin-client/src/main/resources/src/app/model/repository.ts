export class Repository {

    constructor() {}

    public type: string;

    public vcs: string;
    public url: string;
    public branch: string;
    public module: string = "/";

    public username: string;
    public password: string;
}
