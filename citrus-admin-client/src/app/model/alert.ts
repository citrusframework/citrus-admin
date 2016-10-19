export class Alert {

    constructor(type: string,
                message: string,
                autoClear: boolean) {
        this.type = type;
        this.message = message;
        this.autoClear = autoClear;
    }

    public type: string;
    public message: string;
    public autoClear = false;
    public link: Link;

    withLink<Alert>(link: Link) {
        this.link = link;

        return this;
    }
}

export class Link {
    constructor(url: string,
                name: string) {
        this.url = url;
        this.name = name;
    }

    public url: string;
    public name: string;
}