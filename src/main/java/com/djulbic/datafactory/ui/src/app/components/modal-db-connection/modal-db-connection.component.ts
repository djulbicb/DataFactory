import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatSnackBar } from '@angular/material';
import { DbConnection } from 'src/app/model/DbConnection';

@Component({
  selector: 'app-modal-db-connection',
  templateUrl: './modal-db-connection.component.html',
  styleUrls: ['./modal-db-connection.component.css']
})
export class ModalDbConnectionComponent implements OnInit {

  constructor(
    private _snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<ModalDbConnectionComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DbConnection) { }

  ngOnInit() {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  verifyAndSend(data:DbConnection){
    // [mat-dialog-close]="data"
    if( data.driver && data.password && data.url && data.username ){
      this.dialogRef.close(data);
    } else{
      this._snackBar.open("Fill out form data first to create a connection.", null, {duration: 3600, panelClass:['info-snackbar']});
    }
  }


}
