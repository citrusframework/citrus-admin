import {Component,  Input} from 'angular2/core';
import {Message} from "../model/message";
import {NgIf} from "angular2/common";

@Component({
    selector: "test-message",
    template: `<div (click)="toggleMessage()" [class]="message.type == 'OUTBOUND' ? 'clickable message-outbound' : 'clickable message-inbound'">
    <i *ngIf="message.type == 'INBOUND'" class="fa fa-angle-double-left">&nbsp;</i>
    <i class="fa fa-envelope-o">&nbsp;</i>
    <i *ngIf="message.type == 'OUTBOUND'" class="fa fa-angle-double-right">&nbsp;</i>
</div>
<pre *ngIf="open" [style.color]="message.type == 'OUTBOUND' ? '#000099' : '#026ebe'">{{message.data}}</pre>
<hr/>`,
    directives: <any> [ NgIf ]
})
export class TestMessageComponent {
    @Input() message: Message;

    open = false;

    toggleMessage() {
        this.open = !this.open;
    }
}