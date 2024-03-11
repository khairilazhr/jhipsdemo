export interface ITransaction {
  id?: number;
  name?: string;
  title?: string;
  totalprice?: number | null;
  date?: Date | null;
  gender?: string | null;
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public name?: string,
    public title?: string,
    public totalprice?: number | null,
    public date?: Date | null,
    public gender?: string | null
  ) {}
}
