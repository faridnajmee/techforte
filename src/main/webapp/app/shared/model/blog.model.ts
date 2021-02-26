export interface IBlog {
  id?: number;
  name?: string;
  positive?: boolean;
  userLogin?: string;
  userId?: number;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
}

export class Blog implements IBlog {
  constructor(
    public id?: number,
    public name?: string,
    public positive?: boolean,
    public userLogin?: string,
    public userId?: number,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date
  ) {
    this.positive = this.positive || false;
  }
}
