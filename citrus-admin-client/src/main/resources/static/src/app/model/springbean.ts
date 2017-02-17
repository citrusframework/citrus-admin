import {SpringProperty} from "./springproperty";

export class SpringBean {

    constructor() {
        this.properties = [];
    }

    public properties: SpringProperty[];

    public id: string;
    public clazz: string;
}