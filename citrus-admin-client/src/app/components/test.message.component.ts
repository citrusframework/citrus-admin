import {Component,  Input} from 'angular2/core';
import {Message} from "../model/message";
import {NgIf} from "angular2/common";

@Component({
    selector: ".test-message",
    template: `<div (click)="toggleMessage()" [style.color]="message.type == 'OUTBOUND' ? '#000099' : '#026ebe'">
  <span *ngIf="message.type == 'OUTBOUND'"><i class="fa fa-envelope-o">&nbsp;</i> send</span>
  <span *ngIf="message.type == 'INBOUND'"><i class="fa fa-envelope-o">&nbsp;</i> receive</span>
  <pre *ngIf="open">{{message.data}}</pre>
</div>`,
    directives: <any> [ NgIf ]
})
export class TestMessageComponent {
    @Input() message: Message;

    open = true;

    toggleMessage() {
        this.open = !this.open;
    }
}