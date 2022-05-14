package rusting.ctype;

import rusting.Varsr;

//only used for difrent research centers
public class ResearchType extends MappableERContent{

    public ResearchType(String name) {
        super(name);
    }

    @Override
    public ERContentType getContentType() {
        return Varsr.content.getContentType("researchType");
    }

}
