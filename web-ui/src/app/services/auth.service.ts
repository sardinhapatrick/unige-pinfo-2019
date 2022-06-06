import {Subject} from 'rxjs';
import {Injectable} from '@angular/core';
import { HttpHeaders, HttpClient ,HttpParams} from '@angular/common/http';

@Injectable()
export class AuthService {
  model: any= {};
  isAuth = false;
  list: any[];

  signIn() {
    return new Promise(
      (resolve, reject) => {
        setTimeout(
          () => {
            this.isAuth = true;
            console.log(this.isAuth);
            resolve(true);
          }, 2000
        );
      }
    );
  }


  createUser(){
    //POST
  }




  signOut() {
    this.isAuth = false;
  }

  display() {
  return 0

  }

  constructor(private httpClient: HttpClient) {
    console.log(httpClient)
  }

}
