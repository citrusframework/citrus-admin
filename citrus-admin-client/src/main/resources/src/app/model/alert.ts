export type AlertType = "success"|"info"|"warning"|"danger"

export class Alert {

    constructor(type: AlertType,
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

    static danger(message:string, autoclear =false) {
        return new Alert("danger", message, autoclear)
    }

    static info(message:string, autoclear =false) {
        return new Alert("info", message, autoclear)
    }

    static warning(message:string, autoclear =false) {
        return new Alert("warning", message, autoclear)
    }

    static success(message:string, autoclear =false) {
        return new Alert("success", message, autoclear)
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