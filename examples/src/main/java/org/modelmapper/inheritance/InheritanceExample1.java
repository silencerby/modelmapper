package org.modelmapper.inheritance;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.modelmapper.ModelMapper;

public class InheritanceExample1 {

  public static void main(String... args) {
    org.modelmapper.inheritance.C c = new org.modelmapper.inheritance.C(asList(new BaseSrcA("foo"), new BaseSrcB()));

    ModelMapper modelMapper = new ModelMapper();

//    TypeMap<BaseSrc, BaseDest> typeMap =
        modelMapper.createTypeMap(BaseSrc.class, BaseDest.class)
//            .include(BaseSrcA.class, BaseDestA.class)
//            .include(BaseSrcB.class, BaseDestB.class)
    	      ;
    modelMapper.createTypeMap(BaseSrcA.class, BaseDestA.class).includeBase(BaseSrc.class, BaseDest.class);
    modelMapper.createTypeMap(BaseSrcB.class, BaseDestB.class).includeBase(BaseSrc.class, BaseDest.class);

    modelMapper.typeMap(BaseSrcA.class, BaseDest.class).setProvider(request -> new BaseDestA());
    modelMapper.typeMap(BaseSrcB.class, BaseDest.class).setProvider(request -> new BaseDestB());

    CcDTO ccDTO = modelMapper.map(c, CcDTO.class);

    assertEquals(2, ccDTO.getBases().size());
    BaseDest baseDest = ccDTO.getBases().get(0);
    assertTrue(baseDest instanceof BaseDestA);
    assertEquals(((BaseDestA) baseDest).getProperty1(), "foo");
    assertTrue(ccDTO.getBases().get(1) instanceof BaseDestB);
  }
}
