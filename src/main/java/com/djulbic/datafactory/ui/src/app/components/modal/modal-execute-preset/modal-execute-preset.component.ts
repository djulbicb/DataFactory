import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatSnackBar } from '@angular/material';
import { DbConnection } from 'src/app/model/DbConnection';
import { ApiService } from 'src/app/service/api-service.service';


@Component({
  selector: 'app-modal-execute-preset',
  templateUrl: './modal-execute-preset.component.html',
  styleUrls: ['./modal-execute-preset.component.css']
})
export class ModalExecutePresetComponent implements OnInit {
  drivers:string[];

  constructor(
    private apiService:ApiService,
    private _snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<ModalExecutePresetComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DbConnection) { }

  ngOnInit() {
    this.data.url = "jdbc:mysql://localhost:3306";
    this.data.driver = "MYSQL";
    this.data.password="";
    this.data.username="";
    this.apiService.getDatabaseDrivers().subscribe(data=>{
      this.drivers = data;
    })
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  verifyAndSend(data:DbConnection){
    // [mat-dialog-close]="data"
    console.log(data);
    if( data.driver && data.url){
      this.dialogRef.close(data);
    } else{
      this._snackBar.open("Fill out form data first to create a connection.", null, {duration: 3600, panelClass:['info-snackbar']});
    }
  }


}