import {Injectable, Type} from "@angular/core";
import * as SockJS from 'sockjs-client';
import {over, Frame, Client, Message} from "stompjs";
import {Subject} from "rxjs/Subject";
import {Observable} from "rxjs/Observable";
import {Observer} from "rxjs/Observer";
import * as _ from 'lodash'
import {environment} from "../../environments/environment";
import {ReplaySubject} from "rxjs/ReplaySubject";

export function parseBody<T = any>(factory: (mb: any) => T) {
    return (m: Message) => {
        return JSON.parse(m.body) as T;
    }
}

export enum ConnectionStatus {
    NOT_CONNECTED,
    WAITING,
    CONNECTED
}

export class Topic extends Subject<Message> {
    constructor(private connection: Client,
                private topic: string) {
        super();
        connection.subscribe(topic, m => this.next(m))
    }
}

export class StompConnection {
    private debug: boolean = environment.stompDebug;
    private stompClient: Client;
    private topics: Map<string, Topic> = new Map();
    private connectionSubject = new ReplaySubject<StompConnection>();
    private status: ConnectionStatus = ConnectionStatus.NOT_CONNECTED;

    constructor(private url: string) {
        this.stompClient = over(new SockJS(this.url) as WebSocket);

        this.stompClient.debug = _.wrap(this.stompClient.debug, (fn: (...args: string[]) => any, ...args: string[]) => {
            if (this.debug) {
                return fn.apply(this.stompClient, args);
            } else {
                return null;
            }
        }) as (...args: string[]) => any;

    }

    connect() {
        if(this.status == ConnectionStatus.NOT_CONNECTED) {
            this.status = ConnectionStatus.WAITING;
            this.stompClient.connect({} as any,
                (frame: Frame) => {
                    if (frame) {
                        this.status = ConnectionStatus.CONNECTED;
                        this.connectionSubject.next(this);
                    }
                },
                (error: any) => this.connectionSubject.error(error)
            );
        }
        return this.connectionSubject;
    }

    subscribeToTopic(topic: string): Topic {
        if (!this.topics.has(topic)) {
            this.topics.set(topic, new Topic(this.stompClient, topic))
        }
        return this.topics.get(topic);
    }
}

@Injectable()
export class StompConnectionService {

    connections: Map<string, StompConnection> = new Map();

    constructor() {
    }

    getConnection(url: string) {
        if (!this.connections.has(url)) {
            this.connections.set(url, new StompConnection(url))
        }
        return this.connections.get(url);
    }
}