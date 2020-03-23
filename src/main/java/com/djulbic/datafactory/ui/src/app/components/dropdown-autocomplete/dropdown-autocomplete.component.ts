import { Component, OnInit, Input } from '@angular/core';

import {map, startWith} from 'rxjs/operators';
import {combineLatest, Observable, of, from} from 'rxjs';
import {FormControl} from '@angular/forms';
import { State } from 'src/app/model/State';



@Component({
  selector: 'dropdown-autocomplete',
  templateUrl: './dropdown-autocomplete.component.html',
  styleUrls: ['./dropdown-autocomplete.component.css']
})
export class DropdownAutocompleteComponent implements OnInit {

  @Input() input:Observable<any>;
  data:Array<any>;

  ngOnInit() {
    this.input.subscribe((data)=>{
      this.data = data;
    });
  }

  states$: Observable<State[]>;
  filteredStates$: Observable<State[]>;
  filter: FormControl;
  filter$: Observable<string>;
  console = console;
}
