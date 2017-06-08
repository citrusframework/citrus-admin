import {Component, Input} from "@angular/core";
@Component({
    selector:'endpoint-label',
    styles: [`
        .label {
            font-size: 85%;
            padding: .2em;
        }
        i {
            margin-left: 2px;
        }
    `],
    template: `
      <span class="label label-{{type}}">
        <i class="fa fa-share-alt-square"></i>
      </span><span *ngIf="!iconOnly">&nbsp;{{type}}</span>
    `
})
export class EndpointLabelComponent {
    @Input() type:string;
    @Input() iconOnly = false;
}