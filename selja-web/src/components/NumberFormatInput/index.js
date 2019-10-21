import React from 'react';
import NumberFormat from 'react-number-format';

export default function NumberFormatCustom(props) {
    const { inputRef, onChange, ...other } = props;

    return (
        <NumberFormat
            {...other}
            getInputRef={inputRef}
            onValueChange={values => {
                onChange({
                    target: {
                        value: values.value,
                        name: props.name
                    },
                });
            }}
            thousandSeparator
            isNumericString
            suffix={` ${props.suffix}`}
        />
    );
}
