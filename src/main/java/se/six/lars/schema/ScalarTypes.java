package se.six.lars.schema;


import graphql.Assert;
import graphql.GraphQLException;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.TimeZone;

public class ScalarTypes
{
    public static GraphQLScalarType GraphQLDate = new GraphQLScalarType("DateTime", "DateTime type", new Coercing()
    {
        private String dateFormat = "yyyy-MM-dd'T'HH:mm'Z'";
        private final TimeZone timeZone = TimeZone.getTimeZone("UTC");

        @Override
        public Object serialize(Object input)
        {
            if (input instanceof String) {
                return parse((String)input);
            } else if (input instanceof Date) {
                return format((Date)input);
            } else if (input instanceof Long) {
                return new Date((Long)input);
            } else if (input instanceof Integer) {
                return new Date(((Integer)input).longValue());
            } else {
                throw new GraphQLException("Wrong timestamp value");
            }
        }

        @Override
        public Object parseValue(Object input)
        {
            return serialize(input);
        }

        @Override
        public Object parseLiteral(Object input)
        {
            if (!(input instanceof StringValue)) return null;
            return parse(((StringValue)input).getValue());
        }

        private String format(Date input)
        {
            return getSimpleDateFormat().format(input.getTime());
        }

        private Date parse(String input)
        {
            Date date = null;
            try {
                date = getSimpleDateFormat().parse(input);
            } catch (Exception e) {
                throw new GraphQLException("Can not parse input date", e);
            }
            return date;
        }

        private SimpleDateFormat getSimpleDateFormat()
        {
            SimpleDateFormat df = new SimpleDateFormat(dateFormat);
            df.setTimeZone(timeZone);
            return df;
        }
    });
}


