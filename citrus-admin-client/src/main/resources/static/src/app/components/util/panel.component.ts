import {Component, HostBinding, Output, EventEmitter, Input, Directive} from "@angular/core";
@Component({
    selector: 'detail-panel',
    template: `
        <div class="panel panel-default">
            <ng-content select=".panel-heading, [detail-panel-heading]"></ng-content>
            <ng-content select=".panel-body"></ng-content>
            <ng-content select=".panel-footer"></ng-content>
        </div>
    `,
    styles: [`
        :host {
            padding-left: 0;
            /*padding-right: 10px;*/
        }
        
        .panel {
            width: 100%;
            max-width: 100%;
            min-width: 0;
            display: flex;
            flex-direction: column;
        }                   
    `]
})
export class DetailPanelComponent {
    @HostBinding('class')
    get hostClasses() {
        return 'col-xs-12 col-sm-6 col-md-4 col-lg-3'
    }

    @HostBinding('style.display') get flexStyle() {return 'flex'}
    @HostBinding('style.direction') get flexDirectionStyle() {return 'column'}

}

@Directive({
    selector:'.panel-body',
})
export class DetailPanelBodyComponent {
    @HostBinding('style.flex') get c() { return '1 0 auto' }
}

@Component({
    selector: 'div[detail-panel-heading]',
    template: `
      <h3 class="panel-title">            
        <a class="clickable title" (click)="invokeClick($event)" [title]="title">{{ title }}</a>
        <a class="clickable remove" (click)="invokeRemove($event)" title="Remove endpoint"><i class="fa fa-times" style="color: #A50000;"></i></a>
      </h3>
    `,
    styles: [`
        .panel-title {
            display: flex;            
        }               
        
        .title {
            white-space: nowrap;
            overflow: hidden;
            flex-grow: 1;
            text-overflow: ellipsis;        
        }
        
        .remove {
            align-self: flex-end;
        }
    `]
})
export class DetailPanelHeadingComponent {
    @HostBinding('class') get hostClass() {
        return 'panel-heading'
    }

    @Input() title = "";

    @Output() click = new EventEmitter<MouseEvent>();
    @Output() remove = new EventEmitter<MouseEvent>();

    invokeClick($e: MouseEvent) {
        $e.stopPropagation();
        this.click.next($e)
    }

    invokeRemove($e: MouseEvent) {
        $e.stopPropagation();
        this.remove.next($e)
    }

}