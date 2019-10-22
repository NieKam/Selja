import { Container, Button, Fab, MenuItem, Card, CardMedia, TextField, Typography } from '@material-ui/core';
import { Formik, ErrorMessage } from 'formik';
import { previewPhotoUrl } from "../../helpers/routes"
import { withRouter } from 'react-router-dom';
import { withStyles } from "@material-ui/styles"
import * as _ from 'ramda'
import * as adItemApi from "../../helpers/adItemApi"
import * as idHelper from "../../helpers/id"
import NumberFormatInput from '../../components/NumberFormatInput'
import PropTypes from "prop-types"
import React, { Component } from 'react';

const styles = theme => ({
    root: {
        display: "flex",
        flexDirection: "column",
    },
    adDetails: {
        display: "flex",
        justifyContent: "center",
    },
    detailsField: {
        marginTop: theme.spacing(2),
        marginBottom: theme.spacing(2),
        marginRight: theme.spacing(2)
    },
    button: {
        marginTop: theme.spacing(2),
        marginBottom: theme.spacing(2),
    },
    menu: {
        width: 200,
    },
    input: {
        display: 'none',
    },
    card: {
        marginTop: theme.spacing(2),
        marginBottom: theme.spacing(2),
        maxWidth: 345,
    },
    detailsContainer: {
        padding: 0
    },
    cardContainer: {
        display: "flex",
        flexDirection: "column",
        padding: 0
    },
})

class NewItemForm extends Component {
    constructor(props) {
        super(props);
        if (!props.lat && !props.long) {
            this.locationError = "Without known location you can't add new advertisement"
        }
    }

    state = {
        file: null,
        disabled: true
    }

    handlePhotoUpload = (event) => {
        this.setState({ file: event.target.files[0] })
    }

    daysToMs = (days) => {
        return days * 24 * 60 * 60 * 1000
    }

    onFormSubmit = async (values, { setSubmitting }) => {
        setSubmitting(true)
        let addedItem = await adItemApi.createNewAd({
            deviceId: idHelper.getUuid(),
            name: values.name,
            description: values.description,
            price: values.price,
            phone: values.phone,
            duration: this.daysToMs(values.duration),
            lat: this.props.lat,
            long: this.props.long
        }, this.state.file)

        setSubmitting(false)
        if (addedItem) {
            this.props.history.push("/");
        }
    }

    validateForm = (values) => {
        let errors = {}

        if (!values.name) {
            errors.name = "Required"
        }
        if (!values.description) {
            errors.description = "Required"
        }
        if (values.phone.length < 4) {
            errors.phone = 'Phone number cannot has less than 4 digits'
        }

        if (!values.price) {
            errors.price = "Required"
        } else if (values.price == 0.0) {
            errors.price = "Price cannot be zero"
        }

        this.setState({ disabled: !_.isEmpty(errors) })
        return errors
    }

    render() {
        const { classes } = this.props
        return (
            <Formik
                initialValues={{ name: '', description: '', phone: '', duration: 1, price: '' }}
                onSubmit={this.onFormSubmit}
                validate={this.validateForm}
                render={({
                    values,
                    errors,
                    handleChange,
                    isSubmitting,
                    handleSubmit,
                }) => (
                        <form onSubmit={handleSubmit}>
                            <Container maxWidth="lg" className={classes.root}>
                                <TextField
                                    className={classes.textField}
                                    error={errors.name != null}
                                    label="Name"
                                    margin="normal"
                                    name='name'
                                    onChange={handleChange}
                                    placeholder="Name"
                                    value={values.name}
                                    variant="outlined"
                                    inputProps={{
                                        maxLength: 55,
                                    }}
                                />
                                <ErrorMessage name="name" />
                                <Container maxWidth="lg" className={classes.detailsContainer} >
                                    <TextField
                                        className={classes.detailsField}
                                        error={errors.phone != null}
                                        label="Phone Number"
                                        margin="normal"
                                        name='phone'
                                        onChange={handleChange}
                                        placeholder="+123456789"
                                        type="tel"
                                        value={values.phone}
                                        variant="outlined"
                                    />
                                    <TextField
                                        className={classes.detailsField}
                                        helperText="Duration in days"
                                        label="Select"
                                        margin="normal"
                                        name="duration"
                                        onChange={handleChange}
                                        select
                                        value={values.duration}
                                        variant="outlined"
                                        SelectProps={{
                                            MenuProps: {
                                                className: classes.menu,
                                            },
                                        }}
                                    > {[...Array(7)].map((e, i) => <MenuItem key={i} value={i + 1}>{i + 1}</MenuItem>)} </TextField>
                                    <TextField
                                        className={classes.detailsField}
                                        error={errors.price != null}
                                        label="Price"
                                        margin="normal"
                                        name="price"
                                        onChange={handleChange}
                                        placeholder="Price"
                                        value={values.price}
                                        variant="outlined"
                                        InputProps={{
                                            inputComponent: NumberFormatInput,
                                            inputProps: { suffix: "zł" }
                                        }}
                                    />
                                </Container>
                                <TextField
                                    className={classes.textField}
                                    error={errors.description != null}
                                    label="Description"
                                    margin="normal"
                                    multiline
                                    name='description'
                                    onChange={handleChange}
                                    placeholder="Description"
                                    value={values.description}
                                    variant="outlined"
                                    inputProps={{
                                        maxLength: 2500,
                                    }}
                                />
                                <Container maxWidth="lg" className={classes.cardContainer}>
                                    <Card className={classes.card}>
                                        <CardMedia
                                            alt="You ad image"
                                            component="img"
                                            height="300"
                                            image={previewPhotoUrl(this.state.file)}
                                            title="You ad image"
                                        />
                                    </Card>
                                    <input
                                        accept="image/*"
                                        className={classes.input}
                                        id="contained-button-file"
                                        multiple
                                        onChange={this.handlePhotoUpload}
                                        type="file"
                                    />
                                    <label htmlFor="contained-button-file">
                                        <Button variant="outlined" component="span" className={classes.button}>Upload photo</Button>
                                    </label>
                                </Container>
                                <Typography color="error" align="center" variant="h6">{this.locationError}</Typography>
                                <Fab type="submit" variant="extended" aria-label="delete" className={classes.button} disabled={this.state.disabled || isSubmitting} >
                                    Save advertisement
                                </Fab>
                            </Container>
                        </form>
                    )
                } />
        )
    }
}

NewItemForm.propTypes = {
    classes: PropTypes.object.isRequired
}

export default withRouter(withStyles(styles)(NewItemForm))