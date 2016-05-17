import {Component,  Input} from 'angular2/core';
import {Message} from "../model/message";
import {NgIf} from "angular2/common";

@Component({
    selector: "test-message",
    template: `<div *ngIf="message.type == 'OUTBOUND'" class="row">
    <div class="col-lg-6">
        <div (click)="toggleMessage()" class="clickable message-outbound pull-right">
            <i class="fa fa-envelope-o">&nbsp;</i>
            <i class="fa fa-angle-double-right">&nbsp;</i>
        </div>
    </div>
    <div class="col-lg-6"></div>
</div>
<div *ngIf="message.type == 'INBOUND'" class="row">
    <div class="col-lg-6"></div>
    <div class="col-lg-6">
        <div (click)="toggleMessage()" class="clickable message-inbound">
            <i class="fa fa-angle-double-left">&nbsp;</i>
            <i class="fa fa-envelope-o">&nbsp;</i>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-lg-12">
        <pre *ngIf="open" [style.color]="message.type == 'OUTBOUND' ? '#000099' : '#026ebe'">{{message.data}}</pre>
    </div>
</div>
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