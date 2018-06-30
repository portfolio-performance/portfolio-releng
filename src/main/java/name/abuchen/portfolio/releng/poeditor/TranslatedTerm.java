package name.abuchen.portfolio.releng.poeditor;

public class TranslatedTerm extends Term
{
    private final String definition;
    private final String comment;

    public TranslatedTerm(String term, String context, String definition, String comment)
    {
        super(term, context);
        this.definition = definition;
        this.comment = comment;
    }

    public String getDefinition()
    {
        return definition;
    }

    public String getComment()
    {
        return comment;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((definition == null) ? 0 : definition.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        TranslatedTerm other = (TranslatedTerm) obj;
        if (comment == null)
        {
            if (other.comment != null)
                return false;
        }
        else if (!comment.equals(other.comment))
            return false;
        if (definition == null)
        {
            if (other.definition != null)
                return false;
        }
        else if (!definition.equals(other.definition))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "TranslatedTerm [definition=" + definition + ", comment=" + comment + ", toString()=" + super.toString()
                        + "]";
    }

}
