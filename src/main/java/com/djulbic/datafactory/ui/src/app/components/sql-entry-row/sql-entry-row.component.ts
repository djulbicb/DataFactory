import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { ColumnSql } from 'src/app/model/ColumnSql';
import { FormControl } from '@angular/forms';
import {map, startWith} from 'rxjs/operators';
import { Observable } from 'rxjs';
import { MatAutocomplete } from '@angular/material';
import { MethodDTO } from 'src/app/model/MethodDTO';

@Component({
  selector: 'sql-entry-row',
  templateUrl: './sql-entry-row.component.html',
  styleUrls: ['./sql-entry-row.component.css']
})
export class SqlEntryRowComponent implements OnInit {

  @Input() entry: ColumnSql;
  @Input() mapped; 

  selectedValue:MethodDTO;
  
  ifShowDelimiterInput:boolean = false;
  ifShowParamsInput:boolean = false;

  methodSelection;

  @ViewChild("inputParametars", {static:false}) inputParametars:ElementRef;
  @ViewChild("inputDelimiter", {static:false}) inputDelimiter:ElementRef;


  myControl = new FormControl();
  options: string[] = ['One', 'Two', 'Three'];
  filteredOptions: Observable<string[]>;

  constructor() { }

  ngOnInit() {
    console.log(this.entry);
    this.methodSelection = this.mapped[this.entry.type];
    this.options = this.mapped[this.entry.type];
    console.log(this.mapped[this.entry.type]);
    this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        map(value => this._filter(value))
      );

  }
      
  private _filter(value: string): string[] {
    console.log("1");
    console.log(this.myControl);
    if(value.toLocaleLowerCase){
      const filterValue = value.toLowerCase();
      return this.options.filter(option => option["methodName"].toLowerCase().includes(filterValue));
    }
    
  }

  getOptionText(option:MethodDTO) {
    if(option){
      return option.methodName;
    }
  }

  click(){
    console.log("clicked");
    console.log(this.selectedValue);
    console.log(this.ifShowParamsInput);
    console.log(this.ifShowDelimiterInput);
    // console.log(this.inputParametars.nativeElement.value);
    // console.log(this.inputDelimiter.nativeElement.value);
  }

  optionSelected(obj:MethodDTO){
    console.log("sss");
    console.log(obj);
    
    this.selectedValue = obj;

    this.entry.method = Object.assign({}, this.selectedValue);
    //this.entry.method = this.selectedValue;

    if(obj.paramsCount > 0){
      this.ifShowDelimiterInput = true;
      this.ifShowParamsInput = true;
    } else{
      this.ifShowParamsInput = false;
      this.ifShowDelimiterInput = false;
    }

    if(obj.isVarArgs || obj.paramsCount > 1){
      this.ifShowDelimiterInput = true;
    } else{
      this.ifShowDelimiterInput = false;
    } 
  }
}

// https://stackoverflow.com/questions/55099125/angular-autocomplete-is-returning-value-as-object-object