import {Component, Input, Output, EventEmitter, ElementRef, ViewChild, OnInit, forwardRef} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR, FormControl} from "@angular/forms";
import {isNullOrUndefined} from "util";
import * as _ from 'lodash';

export function RestrictValues(values:string[]) {
    return (c:FormControl) => {
        console.log(values, c.value, _.includes(values, c.value))
        return _.includes(values, c.value) ? null : ({
                restrictedValues: {
                    valid: false
                }
            })
    }
}

@Component({
    selector: 'autocomplete',
    styles: [`    
        .dropdown-menu.autocomplete {
            max-height: 650px;
            overflow-x: hidden;
            overflow-y: auto;
            left: auto;
        }
        .dropdown-menu .active > a {
            color: #262626;
        }
        .dropdown-menu li > a:hover {
            background: white;
        }
        .dropdown-menu .active > a:hover {
            background-color: #fcc300;
            background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#fad011), to(#fac300));
            background-image: -webkit-linear-gradient(top, #fad011, #fcc300);
            background-image: linear-gradient(to bottom, #fad011, #fcc300);
        }
    `],
    host: {
        '(document:click)': 'handleDocumentClick($event)',
        '(keydown)': 'handleKeyDown($event)'
    },
    template: `
    <div [class]="addon ? 'input-group' : 'form-group'">
      <input    id="{{id}}" 
                type="text" 
                autocomplete="off" 
                placeholder="{{placeholder}}" 
                class="form-control" 
                [(ngModel)]="query" 
                (ngModelChange)="filter()"
                (focus)="onInputFocus()"
                #inputRef
        >
      <span *ngIf="addon" class="input-group-addon clickable" (click)="showAll()"><i class="fa fa-{{addon}} fa-white"></i></span>
    </div>
    <ul class="dropdown-menu autocomplete" 
        #suggestionListRef
        [style.width]="listStyle.width + 'px'"                
        [style.display]="suggestions?.length > 0 ? 'block': 'none'" 
    >
        <li *ngFor="let suggestion of suggestions" [class.active]="activeSelected === suggestion" (mouseover)="setActiveSelected(suggestion)">
            <a *ngIf="suggestion == 'No elements found'" name="empty-results"><i *ngIf="icon" class="fa fa-{{icon}}"></i> {{suggestion}}</a> 
            <a *ngIf="suggestion != 'No elements found'" name="{{suggestion}}" class="clickable" (click)="select(suggestion)"><i *ngIf="icon" class="fa fa-{{icon}}"></i> {{suggestion}}</a>
        </li>
    </ul>`,
    providers: [{
        provide: NG_VALUE_ACCESSOR,
        useExisting: forwardRef(() => AutoCompleteComponent),
        multi: true
    }],
})
export class AutoCompleteComponent implements OnInit, ControlValueAccessor {

    @Output() selected = new EventEmitter(true);

    @Input() id: string;
    @Input() icon: string;
    @Input() placeholder: string = "";
    @Input() items: string[] = [];
    @Input() addon: string;
    @Input() minLength: number = 3;
    @Input() showImmediately: boolean = true;
    @Input() clearAfterSelect = true;

    query: string = "";
    suggestions: string[] = [];

    @ViewChild('suggestionListRef')
    suggestionListRef: ElementRef;

    @ViewChild('inputRef')
    inputRef: ElementRef;

    listStyle: any = {};

    onChange = (v:any) => {}

    private activeSelected = ""

    constructor(private elementRef: ElementRef) {
    }

    ngOnInit() {
        const {nativeElement:list}:{nativeElement: HTMLUListElement} = this.suggestionListRef;
        const {nativeElement:input}:{nativeElement: HTMLInputElement} = this.inputRef;
        const inputRect = input.getClientRects().item(0);
        this.listStyle = {
            top: inputRect.top + inputRect.height,
            width: inputRect.width,
            left: inputRect.left
        }
    }

    onInputFocus() {
        if (this.showImmediately) {
            this.showAll();
        }
    }

    setActiveSelected(suggestion: string) {
        this.activeSelected = suggestion;
    }

    filter() {
        if (this.query.length >= this.minLength) {
            var substrRegex = new RegExp(this.query, 'i');
            this.suggestions = this.items.filter(item => substrRegex.test(item));
            if (this.suggestions.length == 0) {
                this.suggestions = ["No elements found"];
            }
        } else {
            this.suggestions = [];
        }
        this.onChange(this.query);
    }

    focus() {
        console.log(this.inputRef.nativeElement)
        this.inputRef.nativeElement.focus();
    }

    select(item: string) {
        this.query = this.clearAfterSelect ? "" : item;
        this.suggestions = [];
        this.onChange(this.query);
        this.selected.emit(item);
        this.focus();
    }

    showAll() {
        this.suggestions = this.items;
    }

    handleDocumentClick(event: MouseEvent) {
        var clickedComponent = event.target as Node;
        var inside = false;
        do {
            if (clickedComponent === this.elementRef.nativeElement) {
                inside = true;
            }
            clickedComponent = clickedComponent.parentNode;
        } while (clickedComponent);

        if (!inside) {
            this.suggestions = [];
        }
    }

    handleKeyDown($event: KeyboardEvent) {
        const {keyCode} = $event;
        switch (keyCode) {
            case 40: {// DOWN
                const nextIndex = (this.suggestions.indexOf(this.activeSelected) + 1) % (this.suggestions.length);
                this.activeSelected = this.suggestions[nextIndex]; // if index is out-of-range it will assign undefined
                break;
            }
            case 38: {// UP
                const nextIndex = this.suggestions.indexOf(this.activeSelected) - 1;
                this.activeSelected = this.suggestions[nextIndex < 0 ? this.suggestions.length - 1 : nextIndex]; // if index is out-of-range it will assign undefined
                break;
            }
            case 13: {
                if (this.activeSelected) {
                    this.select(this.activeSelected);
                }
                break;
            }
            case 27: {
                this.activeSelected = "";
                this.suggestions = [];
                break;
            }
        }
    }

    writeValue(obj: any): void {
        if (isNullOrUndefined !== obj)
            this.query = obj;
    }

    registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: any): void {
    }
}