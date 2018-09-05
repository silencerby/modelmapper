package org.modelmapper.functional.inherit;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.testng.annotations.Test;

@Test
public class TypeMapIncludeBaseTest extends AbstractTest {

  interface SrcInterface {
    String getSrc();
  }

  static class SrcA implements SrcInterface {
    String src;

    public SrcA(String src) {
      this.src = src;
    }

    public String getSrc() {
      return src;
    }
  }

  static class SrcB implements SrcInterface {
    String src;

    public SrcB(String src) {
      this.src = src;
    }

    public String getSrc() {
      return src;
    }
  }

  static class SrcC extends SrcB {
    String extField;

    public SrcC(String src) {
      this(src, null);
    }

    public SrcC(String src, String extField) {
      super(src);
      this.extField = extField;
    }

    public String getExtField() {
      return extField;
    }
  }

  static class ListOfSrc {
    List<SrcInterface> list = new ArrayList<SrcInterface>();

    public ListOfSrc(List<SrcInterface> list) {
      super();
      this.list = new ArrayList<SrcInterface>(list);
    }

    public List<SrcInterface> getList() {
      return list;
    }
  }

  interface DestInterface {
    void setDest(String dest);
  }

  static class DestA implements DestInterface {
    String dest;

    public void setDest(String dest) {
      this.dest = dest;
    }
  }

  static class DestB implements DestInterface {
    String dest;

    public void setDest(String dest) {
      this.dest = dest;
    }
  }

  static class DestC extends DestB {
    String extField;

    public String getExtField() {
      return extField;
    }

    public void setExtField(String extField) {
      this.extField = extField;
    }
  }

  static class ListOfDest {
    List<DestInterface> list = new ArrayList<DestInterface>();

    public List<DestInterface> getList() {
      return list;
    }

    public void setList(List<DestInterface> list) {
      this.list = list;
    }
  }

  static class BasePropertyMap extends PropertyMap<SrcInterface, DestInterface> {
    @Override
    protected void configure() {
      map().setDest(source.getSrc());
    }
  }

  public void shouldMappingListOfSubClassSuccess() {
    modelMapper.addMappings(new BasePropertyMap());

    modelMapper.createTypeMap(SrcA.class, DestA.class).includeBase(SrcInterface.class, DestInterface.class);
    modelMapper.createTypeMap(SrcB.class, DestB.class).includeBase(SrcInterface.class, DestInterface.class);
    modelMapper.createTypeMap(SrcC.class, DestC.class).includeBase(SrcInterface.class, DestInterface.class);
    modelMapper.typeMap(SrcA.class, DestInterface.class).setProvider(new Provider<DestInterface>() {
      @Override
      public DestInterface get(ProvisionRequest<DestInterface> request) {
        return new DestA();
      }
    });
    modelMapper.typeMap(SrcB.class, DestInterface.class).setProvider(new Provider<DestInterface>() {
      @Override
      public DestInterface get(ProvisionRequest<DestInterface> request) {
        return new DestB();
      }
    });
    modelMapper.typeMap(SrcC.class, DestInterface.class).setProvider(new Provider<DestInterface>() {
      @Override
      public DestInterface get(ProvisionRequest<DestInterface> request) {
        return new DestC();
      }
    });

    ListOfSrc givenListOfSrc = new ListOfSrc(asList(new SrcA("fooA"), new SrcB("fooB"), new SrcC("fooC", "bar")));

    ListOfDest actualListOfDest = modelMapper.map(givenListOfSrc, ListOfDest.class);

    DestA actualDestA = (DestA) actualListOfDest.getList().get(0);
    assertEquals(actualDestA.dest, "fooA");

    DestB actualDestB = (DestB) actualListOfDest.getList().get(1);
    assertEquals(actualDestB.dest, "fooB");

    DestC actualDestC = (DestC) actualListOfDest.getList().get(2);
    assertEquals(actualDestC.dest, "fooC");
    assertEquals(actualDestC.extField, "bar");
  }

  public void shouldMappingClassSuccess() {
    modelMapper.addMappings(new PropertyMap<SrcInterface, DestInterface>() {
          @Override
          protected void configure() {
            map().setDest(source.getSrc());
          }
        });

    modelMapper.createTypeMap(SrcA.class, DestA.class).includeBase(SrcInterface.class, DestInterface.class);
    modelMapper.createTypeMap(SrcB.class, DestB.class).includeBase(SrcInterface.class, DestInterface.class);
    modelMapper.createTypeMap(SrcC.class, DestC.class).includeBase(SrcInterface.class, DestInterface.class);

    assertEquals(modelMapper.map(new SrcA("foo"), DestA.class).dest, "foo");
    assertEquals(modelMapper.map(new SrcB("foo"), DestB.class).dest, "foo");
    assertEquals(modelMapper.map(new SrcC("foo"), DestC.class).dest, "foo");

    assertNull(modelMapper.map(new SrcA("foo"), DestB.class).dest);
    assertNull(modelMapper.map(new SrcA("foo"), DestC.class).dest);
    assertNull(modelMapper.map(new SrcC("foo"), DestB.class).dest);
  }

  public void shouldMappingInterfaceSuccess() {
    modelMapper.addMappings(new PropertyMap<SrcInterface, DestInterface>() {
          @Override
          protected void configure() {
            map().setDest(source.getSrc());
          }
        });

    modelMapper.createTypeMap(SrcA.class, DestInterface.class)
        .includeBase(SrcInterface.class, DestInterface.class)
        .setProvider(new Provider<DestInterface>() {
          public DestInterface get(ProvisionRequest<DestInterface> request) {
            return new DestA();
          }
    });

    DestInterface dest = modelMapper.map(new SrcA("foo"), DestInterface.class);
    assertTrue(dest instanceof DestA);
    assertEquals(((DestA) dest).dest, "foo");
  }
}