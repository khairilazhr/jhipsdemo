export interface IBook {
  id?: number;
  title?: string;
  price?: number | null;
  publicationDate?: Date | null;
}

export class Book implements IBook {
  constructor(public id?: number, public title?: string, public price?: number | null, public publicationDate?: Date | null) {}
}
