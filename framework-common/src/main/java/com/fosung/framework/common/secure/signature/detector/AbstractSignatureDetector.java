package com.fosung.framework.common.secure.signature.detector;

import lombok.Getter;

/**
 * SignatureDetector的适配器，使用name属性作为不同Detector的唯一标识
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public abstract class AbstractSignatureDetector implements SignatureDetector {

    @Getter
    private String name;

    public AbstractSignatureDetector(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj != null && obj instanceof AbstractSignatureDetector) {
            return this.getName().equals(((AbstractSignatureDetector) obj).getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
