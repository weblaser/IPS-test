package com.ctl.security.ips.common.domain;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.PojoValidator;
import com.openpojo.validation.test.Tester;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import com.openpojo.validation.utils.ValidationHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Kevin.Weber on 10/28/2014.
 */
public class PojoTest {
    // The package to test

    private final List<PojoClass> pojoClasses = new ArrayList<PojoClass>();
    private PojoValidator pojoValidator;

    @Before
    public void setup() {

        final List<String> POJO_PACKAGES = new ArrayList<String>();
        POJO_PACKAGES.add("com.ctl.security.ips.common.domain");
        for (String POJO_PACKAGE : POJO_PACKAGES) {
            pojoClasses.addAll(PojoClassFactory.getPojoClasses(POJO_PACKAGE));
        }

        pojoValidator = new PojoValidator();
        pojoValidator.addTester(new SetterTester());
        pojoValidator.addTester(new GetterTester());
        //Equals
        pojoValidator.addTester(new Tester() {
            @Override
            public void run(PojoClass pojoClass) {
                List<PojoMethod> pojoMethods = pojoClass.getPojoMethods();
                for (PojoMethod pojoMethod : pojoMethods) {
                    if (pojoMethod.getName().equals("equals")) {
                        Object instance = ValidationHelper.getBasicInstance(pojoClass);
                        assertTrue(instance.equals(instance));
                        assertFalse(instance.equals(null));
                        assertFalse(instance.equals(new Object()));
                        Object other = null;
                        try {
                            other = Class.forName(pojoClass.getName()).newInstance();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        assertTrue(instance.equals(other));
                    }
                }

            }
        });
        //HashCode
        pojoValidator.addTester(new Tester() {
            @Override
            public void run(PojoClass pojoClass) {
                List<PojoMethod> pojoMethods = pojoClass.getPojoMethods();
                for (PojoMethod pojoMethod : pojoMethods) {
                    if (pojoMethod.getName().equals("hashCode")) {
                        Object instance = ValidationHelper.getBasicInstance(pojoClass);
                        assertNotNull(instance.hashCode());
                    }
                }
            }
        });
        //ToString
        pojoValidator.addTester(new Tester() {
            @Override
            public void run(PojoClass pojoClass) {
                List<PojoMethod> pojoMethods = pojoClass.getPojoMethods();
                for (PojoMethod pojoMethod : pojoMethods) {
                    if (pojoMethod.getName().equals("toString")) {
                        Object instance = ValidationHelper.getBasicInstance(pojoClass);
                        assertNotNull(instance.toString());
                    }
                }
            }
        });
    }

    @Test
    public void testPojoStructureAndBehavior() {
        for (PojoClass pojoClass : pojoClasses) {
            pojoValidator.runValidation(pojoClass);
        }
    }
}
