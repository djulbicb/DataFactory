import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { State,states } from './model/State';
import { ApiService } from './service/api-service.service';
import { ColumnSql } from './model/ColumnSql';
import { DatabaseRequestConfig } from './model/DatabaseRequestConfig';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormControl } from '@angular/forms';
import {map, startWith} from 'rxjs/operators';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{

  posts:Observable<any>;
  columnRows:ColumnSql[];
  mappedSQLTypesToDataLibraryMethods;


  constructor(private api:ApiService, private _snackBar: MatSnackBar){
    api.getMappedSQLTypesToDataLibraryMethods().subscribe((data)=>{
      this.mappedSQLTypesToDataLibraryMethods = data;
      console.log(this.mappedSQLTypesToDataLibraryMethods);
    });
  }

  ngOnInit(){
    this.posts=this.api.getConfig();
  }

  showTable(config:DatabaseRequestConfig){
    console.log("showTable()");
    this.api.getColumns(config).subscribe((data)=>{
      console.log(data);

      this.columnRows = data;
    });
  }
  
  click(){
    let snackBarRef = this._snackBar.open('Message archived', null, {duration: 3600, panelClass:['blue-snackbar']});
    console.log(this.columnRows);

    this.api.execute(this.columnRows).subscribe((data)=>{
      console.log(data);
    });
  }


  
}
