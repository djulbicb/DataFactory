import { Component, OnInit, ViewChild, QueryList, ViewChildren } from '@angular/core';
import { Observable } from 'rxjs';
import { State,states } from './model/State';
import { ApiService } from './service/api-service.service';
import { ColumnSql } from './model/ColumnSql';
import { DatabaseRequestConfig } from './model/DatabaseRequestConfig';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormControl } from '@angular/forms';
import {map, startWith} from 'rxjs/operators';
import { DatabaseHeaderComponent } from './components/database-header/database-header.component';
import { SnackBarService } from './service/snack-bar-service.service';
import { SqlEntryRowComponent } from './components/sql-entry-row/sql-entry-row.component';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{

  posts:Observable<any>;
  columnRows:ColumnSql[];
  mappedSQLTypesToDataLibraryMethods;

  @ViewChild("header", {static: null}) header:DatabaseHeaderComponent;
  @ViewChildren(SqlEntryRowComponent) sqlEntryRows:QueryList<SqlEntryRowComponent>;

  constructor(private api:ApiService, 
              private snackService:SnackBarService
              ){
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

  executeBatch(numberOfQueries){
    let invalidRows = [];
    this.sqlEntryRows.forEach(row=>{
      if(!row.isValid()){
        invalidRows.push(row.entry.name);
      }
    });

    if(invalidRows.length > 0){
      this.snackService.showError("Check following rows " + invalidRows);
      return;
    }
    

    let databaseConfig = this.header.getDatabaseRequestConfig();
    if(databaseConfig.databaseName !== "" && databaseConfig.databaseTable !==""){
      this.api.execute(databaseConfig, this.columnRows, numberOfQueries).subscribe((data)=>{
        console.log(data.message);
        this.snackService.showInfo(data.message);
      });
    } else {
      this.snackService.showInfo("Select database and table first");
    }
    
  }

  click(){
    console.log(this.columnRows);

    this.api.execute(this.header.getDatabaseRequestConfig(), this.columnRows, 1).subscribe((data)=>{
      console.log(data);
    });
  }

    clearRows(){
console.log("clear");
      this.columnRows = [];
    }

}
