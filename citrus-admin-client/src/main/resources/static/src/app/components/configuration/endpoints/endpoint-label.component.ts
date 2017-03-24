import {Component, Input} from "@angular/core";
@Component({
    selector:'endpoint-label',
    styles: [`
        .label {
            padding: .3em;
        }
        i {
            margin-left: 2px;
        }
    `],
    template: `
      <span class="label label-{{type}}">
        <i class="fa fa-share-alt-square"></i> <span *ngIf="enclosing">{{type}}</span>
      </span> <span *ngIf="!enclosing">&nbsp;{{type}}</span>
    `
})
export class EndpointLabelComponent {
    @Input() type:string;
    @Input() enclosing = false
}