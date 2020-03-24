import { Injectable, NgZone } from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class SnackBarService {

  constructor(private _snackBar: MatSnackBar, private zone: NgZone) {}

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 2000,
    });
  }

  showInfo(message:string){
    this._snackBar.open(message, null, {duration: 3600, panelClass:['info-snackbar']});
  }
  showError(message:string){
    this._snackBar.open(message, null, {duration: 3600, panelClass:['error-snackbar']});
  }
  showSuccess(message:string){
    this._snackBar.open(message, null, {duration: 3600, panelClass:['success-snackbar']});
  }
}
