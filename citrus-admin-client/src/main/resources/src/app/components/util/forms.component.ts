import {Component, Input, EventEmitter, Output, HostBinding, forwardRef} from "@angular/core";
import {FormControl, ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";
@Component({
    selector:'form-group',
    template: `
      <label class="col-sm-2 control-label">{{ label }}</label>
      <div class="col-sm-10">
        <ng-content></ng-content>
      </div>
    `
})
export class FormGroupComponent {
    @Input() control:FormControl;
    @Input() label:string;

    @HostBinding('class') get hostClasses() { return `form-group ${this.control && this.control.errors ? 'has-error':''}`;}
    @HostBinding('style.display') styleDisplay = 'block'
}

@Component({
    selector: 'input-with-addon',
    template: `    
      <input [autofocus]="autofocus" type="text" class="form-control" [(ngModel)]="ngModel" />
      <div class="input-group-addon"  *ngIf="hasMessage">
        <span>{{message}}</span>
      </div>
    `,
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => InputWithAddonComponent),
            multi: true
        }
    ],
    styles: [`
        :host.input-group {
            display: flex;
        }
        :host.input-group .input-group-addon {
            width: auto;
            display: flex;
            justify-content: flex-end;
        }
        :host.input-group .input-group-addon *{
            align-self: center;
        }
    `]
})
export class InputWithAddonComponent implements ControlValueAccessor {
    @Input() control:FormControl;
    @Input() message:string;
    @Input() autofocus = false;
    @Output() ngModelChange = new EventEmitter<any>();

    @HostBinding('class') get hostClasses() { return this.hasMessage ? 'input-group': ''}

    private _ngModel:any;
    get ngModel() {
        return this._ngModel;
    }
    set ngModel(v:any) {
        this._ngModel = v;
        this.ngModelChange.emit(this._ngModel);
    }

    get hasMessage() {
        return this.message;
    }

    writeValue(obj: any): void {
        this.ngModel = obj;
    }

    registerOnChange(fn: any): void {
        this.ngModelChange.subscribe(fn);
    }

    registerOnTouched(fn: any): void {
    }
}
