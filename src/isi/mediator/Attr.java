package isi.mediator;
// __COPYRIGHT_START__
//
// Copyright 2009 University of Southern California. All Rights Reserved.
//
// __COPYRIGHT_END__

public class Attr
{
  protected String name;
  protected AttrType type;


  public Attr(String a_name)
  {
      name = a_name;
      type = new AttrType(AttrType.CHAR_TYPE);
  }

  public Attr(String a_name, String a_type)
  {
      name = a_name;
      type = new AttrType(a_type);
  }
  public Attr(String a_name, int a_type)
  {
      name = a_name;
      type = new AttrType(a_type);
  }

  public Attr(String a_name, AttrType a_type)
  {
      name = a_name;
      type = a_type;
  }

  public String getName() { return name; }
  public int getType() { return type.getType(); }
  public AttrType getAttrType() { return type; }

  @Override
public String toString() 
  {
    return name+ " "+type.typeAsString();
  }

  @Override
public Attr clone() 
  {
    return new Attr(name, type);
  }
}
